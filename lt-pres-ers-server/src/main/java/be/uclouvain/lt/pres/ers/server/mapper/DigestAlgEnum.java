package be.uclouvain.lt.pres.ers.server.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URI;

@AllArgsConstructor
public enum DigestAlgEnum {
    SHA256(URI.create("urn:oid:2.16.840.1.101.3.4.2.1")),
    SHA384(URI.create("urn:oid:2.16.840.1.101.3.4.2.2")),
    SHA512(URI.create("urn:oid:2.16.840.1.101.3.4.2.3"));
    @Getter
    private final URI uri;
}
