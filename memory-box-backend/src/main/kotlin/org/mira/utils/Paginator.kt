package org.mira.utils

object Paginator {


    data class PaginatedResponse<T>(
        val items: List<T>,
        val nextPageToken: String?
    )

    /**
     * Generic pagination function that handles token encoding/decoding.
     *
     * @param pageSize how many items to return per page
     * @param pageToken optional Base64-encoded token from previous page
     * @param decodeToken how to turn a token into a token
     * @param encodeToken how to turn a token into a token
     * @param fetchPage function that fetches data given a token and size
     */
    inline fun <Token, T> paginate(
        pageSize: Int,
        pageToken: String?,
        crossinline decodeToken: (String) -> Token,
        crossinline encodeToken: (Token) -> String,
        crossinline fetchPage: (Token?, Int) -> List<T>,
        crossinline extractToken: (T) -> Token
    ): PaginatedResponse<T> {
        val token = pageToken?.takeIf { it.isNotBlank() }?.let(decodeToken)

        val allItems = fetchPage(token, pageSize + 1)

        val nextToken = if (allItems.size > pageSize) extractToken(allItems.last()) else null
        val nextPageToken = nextToken?.let(encodeToken)

        return PaginatedResponse(
            items = allItems.take(pageSize),
            nextPageToken = nextPageToken
        )
    }
}