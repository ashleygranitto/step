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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
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
    commentEntity.setProperty("text", userComment);

    // Store the Comment Entity in Datastore 
    datastore.put(commentEntity);

    // Redirect back to the HTML page
    response.sendRedirect("/comment.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Retrieve the maximum quantity of comments to display
    String radioValueString = request.getParameter("value");
    int max = Integer.parseInt(radioValueString);

    // Obtain a list of at most 'max' comments
    Query query = new Query("Comment");
    PreparedQuery results = datastore.prepare(query);
    ArrayList<String> comments = formCommentList(results, max);
    
    // Send JSON-converted comments list as the response
    String json = convertToJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  // Return a list containing the text element of at most 'max' Comment Entities 
  private ArrayList<String> formCommentList(PreparedQuery results, int max) {
    ArrayList<String> comments = new ArrayList<>();
    int commentCount = 0;

    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("text");
      comments.add(comment);

      // Ensure no more than 'max' comments are posted
      commentCount++;
      if (commentCount == max) {
          break; 
      }
    }
    return comments; 
  }

  // Converts a String-type ArrayList into a JSON string using the Gson library
  private String convertToJson(ArrayList<String> list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }
}
