using TestApi.Services;

namespace TestApi.Endpoints
{
    public static class AuthEndpoint
    {
        public static void MapAuthEndpoint(this IEndpointRouteBuilder app)
        {
            app.MapPost("/auth", (AuthRequest request,AuthService authService) =>
            {
                var token = authService.Authenticate(
                    request.email,
                    request.password);

                return token is not null
                    ? Results.Ok(new { access_token = token })
                    : Results.Unauthorized();
            });
        }
    }

    public record AuthRequest(string email, string password);
}
