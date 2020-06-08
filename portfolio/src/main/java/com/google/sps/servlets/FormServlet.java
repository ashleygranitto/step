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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns user comments. */
@WebServlet("/handle-comment")
public class FormServlet extends HttpServlet {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve newest user comment and store it as a Comment Entity
    String userComment = request.getParameter("user-comment");
    
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("count", 0);
    commentEntity.setProperty("text", userComment);
    
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
    Query query = new Query("Comment");
    List<Entity> results = 
      datastore.prepare(query).asQueryResultList(FetchOptions.Builder.withLimit(max));
    ArrayList<Comment> comments = formCommentList(results);
    
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
      Comment comment = new Comment(id, text, count);
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
}
