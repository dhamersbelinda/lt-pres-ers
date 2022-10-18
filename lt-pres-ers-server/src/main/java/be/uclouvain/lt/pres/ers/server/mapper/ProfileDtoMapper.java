package be.uclouvain.lt.pres.ers.server.mapper;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import be.uclouvain.lt.pres.ers.model.FormatDto;
import be.uclouvain.lt.pres.ers.model.OperationDto;
import be.uclouvain.lt.pres.ers.model.OperationInputDto;
import be.uclouvain.lt.pres.ers.model.OperationOutputDto;
import be.uclouvain.lt.pres.ers.model.ParameterDto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.server.model.DsbInternationalStringType1;
import be.uclouvain.lt.pres.ers.server.model.MdFormatType;
import be.uclouvain.lt.pres.ers.server.model.MdOperationType;
import be.uclouvain.lt.pres.ers.server.model.MdParameterType;
import be.uclouvain.lt.pres.ers.server.model.MdPolicyByRefType;
import be.uclouvain.lt.pres.ers.server.model.MdPolicyType;
import be.uclouvain.lt.pres.ers.server.model.PresProfileType;
import be.uclouvain.lt.pres.ers.server.model.PresProfileValidityPeriodType;

@Mapper
public interface ProfileDtoMapper {

    @Mapping(target = "pid", source = "profileIdentifier")
    @Mapping(target = "op", source = "operations")
    @Mapping(target = "pol", source = "preservationEvidencePolicy")
    @Mapping(target = "pvp", expression = "java(mapValidityPeriod(dto.getValidFrom(), dto.getValidUntil()))")
    @Mapping(target = "psm", expression = "java(dto.getPreservationStorageModel().getStandardizedValue())")
    @Mapping(target = "pg", source = "preservationGoal")
    @Mapping(target = "ef", source = "evidenceFormat")
    @Mapping(target = "sid", source = "schemeIdentifier")
    @Mapping(target = "spec", source = "specification")
    @Mapping(target = "perp", expression = "java(mapPreservationEvidenceRetentionPeriod(dto.getPreservationEvidenceRetentionPeriod(), dto.getPreservationEvidenceRetentionDuration()))")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "eed", ignore = true)
    @Mapping(target = "ext", ignore = true)
    PresProfileType toPresProfileType(ProfileDto dto);

    @Mapping(target = "spec", source = "specification")
    @Mapping(target = "desc", source = "description")
    @Mapping(target = "in", source = "inputs")
    @Mapping(target = "out", source = "outputs")
    @Mapping(target = "ext", ignore = true)
    @Mapping(target = "opt", ignore = true)
    @Mapping(target = "xsd", ignore = true)
    MdOperationType toMdOperationType(OperationDto dto);

    @Mapping(target = "desc", source = "description")
    @Mapping(target = "form", source = "format")
    @Mapping(target = "ext", ignore = true)
    @Mapping(target = "spec", ignore = true)
    @Mapping(target = "xsd", ignore = true)
    MdParameterType toMdParameterType(OperationInputDto dto);

    default List<MdFormatType> toMdFormatTypeList(final FormatDto dto) {
        return List.of(this.toMdFormatType(dto));
    }

    @Mapping(target = "fid", source = "formatId")
    @Mapping(target = "format", source = "parameters")
    @Mapping(target = "def", ignore = true)
    @Mapping(target = "desc", ignore = true)
    @Mapping(target = "ext", ignore = true)
    @Mapping(target = "spec", ignore = true)
    @Mapping(target = "trfo", ignore = true)
    MdFormatType toMdFormatType(FormatDto dto);

    @Mapping(target = "form", source = "format")
    @Mapping(target = "desc", ignore = true)
    @Mapping(target = "ext", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "spec", ignore = true)
    @Mapping(target = "xsd", ignore = true)
    MdParameterType toMdParameterType(ParameterDto dto);

    @Mapping(target = "desc", source = "description")
    @Mapping(target = "form", ignore = true)
    @Mapping(target = "ext", ignore = true)
    @Mapping(target = "spec", ignore = true)
    @Mapping(target = "xsd", ignore = true)
    MdParameterType toMdParameterType(OperationOutputDto dto);

    default String toString(final URI uri) {
        return uri.toString();
    }

    default List<String> toStringList(final URI uri) {
        return List.of(uri.toString());
    }

    default List<MdPolicyType> topreservationEvidencePolicy(final URI uri) {
        return List.of(new MdPolicyType().type(URI.create("http://uri.etsi.org/19512/policy/preservation-evidence"))
                .pbref(new MdPolicyByRefType().polid(uri.toString())));
    }

    default PresProfileValidityPeriodType mapValidityPeriod(final OffsetDateTime validFrom,
            final OffsetDateTime validUnti) {
        return new PresProfileValidityPeriodType().vfrom(validFrom).vuntl(validUnti);
    }

    default List<MdFormatType> toEvidenceFormat(final URI uri) {
        return List.of(new MdFormatType().fid(uri.toString()));
    }

    default String mapPreservationEvidenceRetentionPeriod(final Period preservationEvidenceRetentionPeriod,
            final Duration preservationEvidenceRetentionDuration) {
        final String period = preservationEvidenceRetentionPeriod.toString();
        final String duration = preservationEvidenceRetentionDuration.toString();
        // Remove leading P so that it is compliant with XSD/ISO-8601 duration datatype.
        return period + duration.substring(1);
    }

    default List<DsbInternationalStringType1> toDsbInternationalStringType1List(final String str) {
        return List.of(new DsbInternationalStringType1().value(str).lang("EN"));
    }
}
