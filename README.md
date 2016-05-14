# net.yetamine.sova.maps #

This repository provides an extension of [net.yetamine.sova](http://github.com/pdolezal/net.yetamine.sova) library for use with the standard `Map` interface, turning a common `Map` into a type-safe heterogeneous container easily.


## Examples ##

Adapting any `Map` instance into a type-safe heterogeneous container is especially useful for various configuration objects and processing contexts. Such a container can become a convenient and extensible replacement for classical Java beans; unlike a bean, the container works as a collection with implementation details provided by adaptation strategies, which makes it safer, avoids relying on reflection magic heavily and prevents leaking many implementation details.

Let's have a context instance that offers the type-safe heterogeneous container interface:

```{java}
// Assuming that the key constants are defined elsewhere, e.g., in UserProfile and Hooks
final String userId = context.get(UserProfile.IDENTIFIER);
final X509Certificate[] certs = context.get(UserProfile.CERTIFICATES);
context.get(Hooks.AUTHENTICATION).authenticate(userId, certs);
```


## Prerequisites ##

For building this project is needed:

* JDK 8 or newer.
* Maven 3.3 or newer.

For using the built library is needed:

* JRE 8 or newer.


## Acknowledgments ##

A special thank belongs to [Atos](http://atos.net/). The development of this library would be much slower without their support which provided a great opportunity to verify the library practically and improve it according to the experience.

Another thank belongs to *davej* from [project77.org](http://project77.org/) for the permission to use his owl picture as the logo for this project. Why an owl? Because it is so cute and because *sova* means *an owl* in Czech.


## Licensing ##

The project is licensed under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0). Contributions to the project are welcome and accepted if they can be incorporated without the need of changing the license or license conditions and terms.


[![Yetamine logo](https://github.com/pdolezal/net.yetamine/raw/master/about/Yetamine_small.png "Our logo")](https://github.com/pdolezal/net.yetamine/blob/master/about/Yetamine_large.png)
[![Sova logo](https://github.com/pdolezal/net.yetamine.sova/raw/8677011f54f4fcfda8be987a461f8109bfbd1308/about/sova_tiny.png "Project logo")](https://github.com/pdolezal/net.yetamine.sova/blob/8677011f54f4fcfda8be987a461f8109bfbd1308/about/sova_large.png)
