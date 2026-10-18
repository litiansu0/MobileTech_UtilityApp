package au.edu.jcu.assessment.utilityapp.data.remote.dto

/** JSON returned by Open-Meteo `/v1/search`. */
data class GeocodingResponse(
    // The API leaves out "results" entirely when nothing matches.
    val results: List<GeocodingResultDto>?,
)

data class GeocodingResultDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    /** State or province, e.g. "Queensland". */
    val admin1: String?,
)
