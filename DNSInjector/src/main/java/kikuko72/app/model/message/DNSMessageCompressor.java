package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.name.LabelUnit;
import kikuko72.app.model.record.identifier.name.PointerLabel;
import kikuko72.app.service.DNS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2016/07/02.
 */
class DNSMessageCompressor {
    static byte[] compress(Header header, List<RecordKey> queries, List<ResourceRecord> records) {
        byte[] ret = new byte[DNS.DNS_UDP_MAX_BYTES];
        int cursor = 0;
        cursor = putBytes(ret, header.bytes(), cursor);
        for (RecordKey query: queries) {
            List<LabelUnit> labels = compressLabel(ret, cursor, query.getLabels());
            RecordKey compressed = query.createCompressedKey(labels);
            cursor = putBytes(ret, compressed.bytes(), cursor);
        }
        for (ResourceRecord record: records) {
            RecordKey key = record.getRecordKey();
            List<LabelUnit> labels = compressLabel(ret, cursor, key.getLabels());
            RecordKey compressedKey = key.createCompressedKey(labels);
            ResourceRecord compressed = record.createCompressedRecord(compressedKey);
            cursor = putBytes(ret, compressed.bytes(), cursor);
        }
        return BytesTranslator.trim(ret);
    }

    private static int putBytes(byte[] buffer, byte[] data, int cursor) {
        System.arraycopy(data, 0, buffer, cursor, data.length);
        return cursor + data.length;
    }

    private static List<LabelUnit> compressLabel(byte[] previous, int endOffset, List<LabelUnit> labels) {
        LabelUnit lastLabel = labels.get(labels.size() - 1);
        // 圧縮されていないラベル列であれば終端は空ラベル
        assert lastLabel.isEmpty();

        List<Integer> provisionalCandidates = new ArrayList<Integer>();
        for(int i = 0; i < endOffset; i++) {
            provisionalCandidates.add(i);
        }
        for(int i = 0; i < labels.size(); i++) {
            int index = labels.size() - 1 - i;
            LabelUnit label = labels.get(index);
            List<Integer> newCandidates = collectCandidateOffsets(previous, label.bytes(), provisionalCandidates);
            if(newCandidates.isEmpty()) {
                if(i <= 1) { // i = 1 以前の時は見つかっていても空ラベルまでなので圧縮するには不適当
                    return labels;
                } else {
                    int offset = provisionalCandidates.get(0);
                    List<LabelUnit> compressed = labels.subList(0, index);
                    compressed.add(new PointerLabel(previous, offset));
                    return compressed;
                }
            }
            provisionalCandidates = newCandidates;
        }
        int offset = provisionalCandidates.get(0);
        List<LabelUnit> compressed = new ArrayList<LabelUnit>();
        compressed.add(new PointerLabel(previous, offset));
        return compressed;
    }

    private static List<Integer> collectCandidateOffsets(byte[] previous, byte[] label, List<Integer> previousCandidates) {
        List<Integer> candidateOffsets = new ArrayList<Integer>();
        for(int index : previousCandidates) {
            int newCandidate = index - label.length;
            if (newCandidate > -1 && findLabel(previous, label, newCandidate)) {
                candidateOffsets.add(newCandidate);
            }
        }
        return candidateOffsets;
    }

    private static boolean findLabel(byte[] previous, byte[] label, int startOffset) {
        if (previous.length < startOffset + label.length) {
            return false;
        }
        for(int i = 0; i < label.length; i++) {
            if (previous[startOffset + i] != label[i]) {
                return false;
            }
        }
        return true;
    }
}
