package devarea.global.cache.tools;

import java.util.Objects;

public abstract class CachedObject<T> {

    protected String object_id;
    protected T object_cached;
    protected long last_fetch;

    public CachedObject(final T object_cached, final String object_id, final long last_fetch) {
        this.object_cached = object_cached;
        this.object_id = object_id;
        this.last_fetch = last_fetch;
    }

    public CachedObject(final String object_id) {
        this.object_id = object_id;
        this.object_cached = null;
        this.last_fetch = 0;
    }

    public T get() {
        if (this.object_cached == null || needToBeFetch())
            return fetch();
        return this.object_cached;
    }

    protected boolean needToBeFetch() {
        return (System.currentTimeMillis() - this.last_fetch) > 600000;
    }

    public abstract T fetch();

    public T watch() {
        if (this.object_cached == null)
            this.fetch();
        return this.object_cached;
    }

    public void use(final T object_cached, final String object_id) throws Exception {
        if (this.object_id.equals(object_id)) {
            this.object_cached = object_cached;
            this.last_fetch = System.currentTimeMillis();
        } else
            throw new Exception("Wrong member usage !");
    }

    public void reset() {
        this.last_fetch = 0;
    }

    @Override
    public boolean equals(Object o) {
        return Objects.equals(object_id, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object_id, object_cached, last_fetch);
    }
}
