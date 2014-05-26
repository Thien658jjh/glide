package com.bumptech.glide.volley;

import android.content.Context;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.bumptech.glide.loader.GlideUrl;
import com.bumptech.glide.loader.bitmap.model.GenericLoaderFactory;
import com.bumptech.glide.loader.bitmap.model.ModelLoader;
import com.bumptech.glide.loader.bitmap.model.ModelLoaderFactory;
import com.bumptech.glide.loader.bitmap.resource.ResourceFetcher;

import java.io.InputStream;

/**
 *  A simple model loader for fetching images for a given url
 */
public class VolleyUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    public interface FutureFactory {
        public VolleyRequestFuture<InputStream> build();
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private final FutureFactory futureFactory;
        private RequestQueue requestQueue;

        public Factory(RequestQueue requestQueue) {
            this(requestQueue, new DefaultFutureFactory());
        }

        public Factory(RequestQueue requestQueue, FutureFactory futureFactory) {
            this.requestQueue = requestQueue;
            this.futureFactory = futureFactory;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new VolleyUrlLoader(requestQueue, futureFactory);
        }

        @Override
        public void teardown() { }
    }

    private final RequestQueue requestQueue;
    private final FutureFactory futureFactory;

    public VolleyUrlLoader(RequestQueue requestQueue, FutureFactory futureFactory) {
        this.requestQueue = requestQueue;
        this.futureFactory = futureFactory;
    }

    @Override
    public ResourceFetcher<InputStream> getResourceFetcher(GlideUrl url, int width, int height) {
        return new VolleyStreamFetcher(requestQueue, url.toString(), getRetryPolicy(), futureFactory.build());
    }

    @Override
    public String getId(GlideUrl url) {
        return url.toString();
    }

    protected RetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy();
    }

    private static class DefaultFutureFactory implements FutureFactory {
        @Override
        public VolleyRequestFuture<InputStream> build() {
            return VolleyRequestFuture.newFuture();
        }
    }

}
