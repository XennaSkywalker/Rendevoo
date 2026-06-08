OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url("https://api.themoviedb.org/3/search/movie?query=jab%20we%20met&include_adult=false&language=en-US&page=1")
  .get()
  .addHeader("accept", "application/json")
  .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5MTI3MWY3YjY1MGFjODcwYzE0NDZlNjdiMGIxMWEyZSIsIm5iZiI6MTc3NTYyMzYwNC4xNTgwMDAyLCJzdWIiOiI2OWQ1ZGRiNDczN2E4OTc2MjFjYzA1OTgiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.GvkaK9whdi2ImYoce2iWwljuklgyuu1wS7lY1j7QI3g")
  .build();

Response response = client.newCall(request).execute();