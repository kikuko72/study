package kikuko72.app.model.message;

/**
 * Created by User on 2016/09/11.
 */
class ScanResult<E> {
    private final E element;
    private final int cursor;

    ScanResult(E element, int cursor) {
        this.element = element;
        this.cursor = cursor;
    }

    E getElement() {
        return element;
    }

    int getCursor() {
        return cursor;
    }
}
