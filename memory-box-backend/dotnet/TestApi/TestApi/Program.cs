using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using TestApi.Endpoint;
using TestApi.Endpoints;
using TestApi.Services;

var jwtKey = "THIS_IS_A_DEV_ONLY_SECRET_KEY_CHANGE_LATER";
var jwtIssuer = "TestApi";
var jwtAudience = "TestApi";

var builder = WebApplication.CreateBuilder(args);

// Services
builder.Services.AddScoped<AuthService>();

builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = jwtIssuer,
            ValidAudience = jwtAudience,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(jwtKey))
        };
    });

builder.Services.AddAuthorization();

var app = builder.Build();

// ?? REQUIRED
app.UseAuthentication();
app.UseAuthorization();

// Endpoints
app.MapHealthEndpoint();
app.MapAuthEndpoint();

app.Run();
