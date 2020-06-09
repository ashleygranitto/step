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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class UpdateLoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    UserService userService = UserServiceFactory.getUserService();
    response.setContentType("text/html");

    if (userService.isUserLoggedIn()) {
      // Create login URL that redirects user to the about page
      String logoutUrl = userService.createLogoutURL("/account.html");
      
      // Return personal welcome message and allow user to log out
      String userEmail = userService.getCurrentUser().getEmail();
      String body = "<p>Hello " + userEmail + "!</p><p>Logout <a href=\"" + logoutUrl + "\">Here</a>.</p>"; 
      response.getWriter().println(body);
    } else {
      // Create login URL that redirects user to the about page
      String loginUrl = userService.createLoginURL("/account.html");

      // Return generic welcome message and allow user to log in 
      String body = "<p>Hello stranger.</p>" + "<p>Login <a href=\"" + loginUrl + "\">Here</a>.</p>"; 
      response.getWriter().println(body);
    }
  }
}
