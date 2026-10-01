const CACHE_NAME = "afro-builders-grade-calculator-v1";
const APP_SCOPE_URL = self.registration.scope;
const APP_SHELL_URL = APP_SCOPE_URL;
const PRECACHE_URLS = [
    APP_SHELL_URL,
    new URL("manifest.webmanifest", APP_SCOPE_URL).href,
    new URL("icon.svg", APP_SCOPE_URL).href
];

self.addEventListener("install", (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then((cache) => cache.addAll(PRECACHE_URLS))
            .then(() => self.skipWaiting())
    );
});

self.addEventListener("activate", (event) => {
    event.waitUntil(
        caches.keys()
            .then((cacheNames) => Promise.all(
                cacheNames
                    .filter((cacheName) =>
                        cacheName.startsWith("afro-builders-grade-calculator-") &&
                        cacheName !== CACHE_NAME
                    )
                    .map((cacheName) => caches.delete(cacheName))
            ))
            .then(() => self.clients.claim())
    );
});

self.addEventListener("fetch", (event) => {
    const request = event.request;
    const requestUrl = new URL(request.url);

    if (request.method !== "GET" || requestUrl.origin !== self.location.origin) {
        return;
    }

    if (request.mode === "navigate") {
        event.respondWith(
            fetch(request)
                .then(async (response) => {
                    if (response.ok) {
                        const cache = await caches.open(CACHE_NAME);
                        await cache.put(APP_SHELL_URL, response.clone());
                    }
                    return response;
                })
                .catch(async () => {
                    const cachedPage =
                        (await caches.match(request, { ignoreSearch: true })) ||
                        (await caches.match(APP_SHELL_URL));
                    return cachedPage || new Response("The calculator is not available offline yet.", {
                        status: 503,
                        headers: { "Content-Type": "text/plain; charset=utf-8" }
                    });
                })
        );
        return;
    }

    event.respondWith(
        caches.match(request)
            .then((cachedResponse) => cachedResponse || fetch(request))
    );
});
