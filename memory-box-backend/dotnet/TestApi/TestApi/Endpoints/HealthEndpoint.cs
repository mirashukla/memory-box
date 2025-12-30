namespace TestApi.Endpoint
{
    public static class HealthEndpoint
    {
        public static void MapHealthEndpoint(this IEndpointRouteBuilder app)
        {
            var createdAt = DateTime.UnixEpoch;
            app.MapGet("/health", () => Results.Ok(createdAt));
        }
    }
}
