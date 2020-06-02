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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns nametag text. */
@WebServlet("/handle-comment")
public class FormServlet extends HttpServlet {

  private ArrayList<String> comments = new ArrayList<>(); 

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Add newest user comment to the list of comments on the feed
    String userComment = request.getParameter("user-comment");
    comments.add(userComment); 

    // Redirect back to the HTML page.
    response.sendRedirect("/comment.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Send JSON-converted comments list as the response
    String json = convertToJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  // Converts a String-type ArrayList into a JSON string using the Gson library. 
  private String convertToJson(ArrayList<String> list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }
}
