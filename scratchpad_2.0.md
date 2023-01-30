(juste des notes pour ne pas oublier et pour avoir un aperçu)

## General notes

We first consider the case where the PO we receive is not an Evidence (?)  
&rarr; we store the hashes and periodically renew their TSs

store hash-trees and timestamps in DB  
At what stage are the Evidence Records created ?
- when do we create the reduced hash-tree ?
- when do we piece together the ATSS ?
  - should the mapper do that ?
- (should additional info be added (EncryptionInformation, SupportingInformation) ? )  
- do we really need to store the POs as previously ?
  - technically (from what i know) all we need is the digests

### Provisional plan : (some parts can be done concurrently)
- implement model classes (Storage structure)
- implement query (retrieval for one POID)
- implement piecing together the query parts
    - including adding info that isn't part of the *main* structure
- map through dto and into API
- implement server calls

### Structure Hierarchy of Evidence Record
```xml
<EvidenceRecord Version><!--Section 6-->
  <EncryptionInformation>
     <EncryptionInformationType><!--Students grades are uploaded by months-->
     <EncryptionInformationValue>
  </EncryptionInformation> ?
  <SupportingInformationList>
     <SupportingInformation Type /> +
  </SupportingInformationList> ?
  <ArchiveTimeStampSequence>
     <ArchiveTimeStampChain Order>
        <DigestMethod Algorithm />
        <CanonicalizationMethod Algorithm />
        <ArchiveTimeStamp Order>
           <HashTree /> ?
           <TimeStamp>
              <TimeStampToken Type />
              <CryptographicInformationList>
                 <CryptographicInformation Order Type /> +
              </CryptographicInformationList> ?
           </TimeStamp>
           <Attributes>
              <Attribute Order Type /> +
           </Attributes> ?
        </ArchiveTimeStamp> +
     </ArchiveTimeStampChain> +
  </ArchiveTimeStampSequence>
</EvidenceRecord>
```

## DB

#### DB Connection

#### DB Queries

## Core

#### Model

#### Repository

#### Service

#### Core Mapper

## Model

#### Dto Model

## Server

#### Delegates

#### Server Mapper

#### APIs