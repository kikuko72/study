package kikuko72.app.model.record.identifier.name;

/**
 * Created by User on 2016/07/03.
 */
public interface LabelUnit {
    boolean isEmpty();
    boolean isPointer();
    boolean hasNextLabel();
    int length();
    byte[] bytes();
}
