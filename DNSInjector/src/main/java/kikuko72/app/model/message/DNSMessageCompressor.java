package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.Type;
import kikuko72.app.model.record.identifier.name.LabelUnit;
import kikuko72.app.model.record.identifier.name.RecordName;
import kikuko72.app.service.DNS;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * DNSメッセージの圧縮を行うクラスです。
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
            assert key.getRecordType().equals(record.getRecordValue().getRecordType());
            if(compressedKey.isType(Type.C_NAME)) {
                cursor = putBytes(ret, compressedKey.bytes(), cursor);
                cursor = putBytes(ret, record.getTtl(), cursor);
                RecordName name = record.getRecordValue().getCNameData();
                byte[] compressedLabel = new RecordName(compressLabel(ret, cursor, name.getLabels())).bytes();
                cursor = putBytes(ret, BytesTranslator.intToTwoBytes(compressedLabel.length), cursor);
                cursor = putBytes(ret, compressedLabel, cursor);
            } else {
                ResourceRecord compressed = record.createCompressedRecord(compressedKey);
                cursor = putBytes(ret, compressed.bytes(), cursor);
            }
        }
        return Arrays.copyOfRange(ret, 0, cursor);
    }

    private static int putBytes(byte[] buffer, byte[] data, int cursor) {
        System.arraycopy(data, 0, buffer, cursor, data.length);
        return cursor + data.length;
    }

    private static List<LabelUnit> compressLabel(byte[] previous, int endOffset, List<LabelUnit> labels) {
        if (labels.size() <=1) { // ラベル1つ→単独の空ラベル(または単独のポインタ)であるため、圧縮の必要なし
            return labels;
        }
        LabelUnit lastLabel = labels.get(labels.size() - 1);
        // 圧縮されていないラベル列であれば終端は空ラベル
        assert lastLabel.isEmpty();

        List<Integer> provisionalCandidates = new ArrayList<Integer>();
        for(int i = 0; i < endOffset; i++) {
            provisionalCandidates.add(i);
        }
        for(int i = 0; i < labels.size(); i++) {
            int index = labels.size() - 1 - i;
            List<LabelUnit> subLabels = labels.subList(index, labels.size());
            List<Integer> newCandidates = collectCandidateOffsets(previous, labelsToBytes(subLabels), provisionalCandidates, endOffset);
            if(newCandidates.isEmpty()) {
                if(i <= 1) { // i = 1 以前の時は見つかっていても空ラベルまでなので圧縮するには不適当
                    return labels;
                } else {
                    int offset = provisionalCandidates.get(0);
                    List<LabelUnit> compressed = labels.subList(0, index + 1);
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

    private static List<Integer> collectCandidateOffsets(byte[] previous, byte[] labels, List<Integer> previousCandidates, int endOffset) {
        List<Integer> candidateOffsets = new ArrayList<Integer>();
        Map<Integer, Integer> pointerOffsets = collectPointerList(previous, endOffset);
        for(int index : previousCandidates) {
            int newCandidate = index - (labels[0] + 1); // 前回の候補から増えたラベルの長さ分だけ戻す。ラベル先頭に入っているラベル長の情報を利用
            if (newCandidate > -1 && findLabels(previous, labels, newCandidate)) {
                candidateOffsets.add(newCandidate);
                Integer pointer = pointerOffsets.get(newCandidate);
                if (pointer != null) {
                    candidateOffsets.add(pointer);
                }
            }
        }
        return candidateOffsets;
    }

    private static Map<Integer, Integer> collectPointerList(byte[] previous, int endOffset) {
        Map<Integer, Integer> pointerOffsets = new HashMap<Integer, Integer>();
        for(int i = 0; i < endOffset - 1; i++) {
            if (BytesTranslator.unSign(previous[i]) >= PointerLabel.MINIMUM_POINTER_HEAD) {
                // indexを参照位置に変更
                int index = (BytesTranslator.unSign(previous[i]) - PointerLabel.MINIMUM_POINTER_HEAD) * 0x100
                        + BytesTranslator.unSign(previous[i + 1]);
                if (index < endOffset) {
                    pointerOffsets.put(index, i);
                }
            }
        }
        return pointerOffsets;
    }

    private static boolean findLabels(byte[] previous, byte[] labels, int startOffset) {
        if (previous.length < startOffset + labels.length) {
            return false;
        }
        int index = startOffset;
        for(int i = 0; i < labels.length; i++) {
            if (index + 1 <= previous.length &&
                    BytesTranslator.unSign(previous[index]) >= PointerLabel.MINIMUM_POINTER_HEAD) {
                    // indexを参照位置に変更
                    index = (BytesTranslator.unSign(previous[index]) - PointerLabel.MINIMUM_POINTER_HEAD) * 0x100
                            + BytesTranslator.unSign(previous[index + 1]);
            }
            if (index >= previous.length || previous[index] != labels[i]) {
                return false;
            }
            index++;
        }
        return true;
    }

    private static byte[] labelsToBytes(List<LabelUnit> labels) {
        ByteBuffer buffer = ByteBuffer.allocate(countLength(labels));
        for(LabelUnit label : labels) {
            buffer.put(label.bytes());
        }
        return buffer.array();
    }

    private static int countLength(List<LabelUnit> labels) {
        int ret = 0;
        for(LabelUnit label : labels) {
            ret += label.length();
        }
        return ret;
    }
}
