package kikuko72.app.service;

import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.value.RecordValue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by User on 2016/09/11.
 */
class RecordStore {
    private final ConcurrentHashMap<RecordKey, RecordValue> store;
    private final ExecutorService e = Executors.newSingleThreadExecutor();

    RecordStore(String hostsFilePath) throws IOException {
        File hosts = new File(hostsFilePath);
        store = HostsReader.parseHosts(hosts.toURI().toURL().openStream());
    }

    RecordStore(ConcurrentHashMap<RecordKey, RecordValue> store) {
        this.store = store;
    }

    RecordValue get(RecordKey key) {
        return store.get(key);
    }

    void cache(List<ResourceRecord> records) {
        e.execute(new CacheTask(store, records));
    }

    private class CacheTask implements Runnable {
        private final ConcurrentHashMap<RecordKey, RecordValue> store;
        private final List<ResourceRecord> records;

        CacheTask(ConcurrentHashMap<RecordKey, RecordValue> store, List<ResourceRecord> records) {
            this.store = store;
            this.records = records;
        }

        @Override
        public void run() {
            if (records.size() == 0) {
                return;
            }
            for(ResourceRecord record : records) {
                // 後から来たレコードに上書かれるが気にしない
                store.put(record.getRecordKey(), record.getRecordValue());
            }
        }
    }
}
