package kikuko72.app.model.record.value;

/**
 * リソースレコードのRDataのインターフェースです
 * Created by User on 2016/09/05.
 */
public interface RData {
    int length();
    byte[] rdLength();
    byte[] bytes();
}
