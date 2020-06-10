// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns user comments. */
@WebServlet("/handle-comment")
public class FormServlet extends HttpServlet {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve newest user comment and store it as a Comment Entity
    String userComment = request.getParameter("user-comment");
    String userEmail = userService.getCurrentUser().getEmail();

    // Get the URL of the image that the user uploaded to Blobstore.
    String imageUrl = getUploadedFileUrl(request, "image");
    
    Entity commentEntity = new Entity("ImageComment");
    commentEntity.setProperty("count", 0);
    commentEntity.setProperty("text", userComment);
    commentEntity.setProperty("email", userEmail);
    commentEntity.setProperty("url", imageUrl); 
    
    // Store the Comment Entity in Datastore 
    datastore.put(commentEntity);

    // Redirect back to the HTML page
    response.sendRedirect("/comment.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve the maximum quantity of comments to display
    String radioValueString = request.getParameter("quantity");

    // Ensure quantity parameter is an integer
    int max = 0; 
    try {
      max = Integer.parseInt(radioValueString);
    } catch (NumberFormatException e) {
      throw new IOException("Quantity is not an integer.");
    }

    // Ensure quantity parameter is of one of the three permitted amounts
    if (max != 3 && max != 5 && max != 10) {
      response.sendError(400, "Quantity may only have the value 3, 5, or 10.");
    }

    // Obtain a list of at most 'max' comments
    Query query = new Query("ImageComment");
    List<Entity> results = 
      datastore.prepare(query).asQueryResultList(FetchOptions.Builder.withLimit(max));
    ArrayList<Comment> comments = formCommentList(results);

    System.out.println(comments);
    
    // Send JSON-converted comments list as the response
    String json = convertToJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  // Return a list containing the text element of at most 'max' Comment Entities 
  private ArrayList<Comment> formCommentList(List<Entity> results) {
    ArrayList<Comment> comments = new ArrayList<>();
    for (Entity entity : results) {
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty("text");
      long count = (long) entity.getProperty("count");
      String email = (String) entity.getProperty("email");
      String url = (String) entity.getProperty("url");
      Comment comment = new Comment(id, text, count, email, url);
      comments.add(comment);
    }
    return comments; 
  }

  // Converts a String-type ArrayList into a JSON string using the Gson library
  private String convertToJson(ArrayList<Comment> list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }

  // Returns a URL that points to the uploaded file, or null if the user didn't upload a file
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // Return null if user submits form without selecting a file (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Get the key of the file input
    BlobKey blobKey = blobKeys.get(0);

    // Return null if user submits form without selecting a file (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // Use ImagesService to get a URL that points to the uploaded file
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}
