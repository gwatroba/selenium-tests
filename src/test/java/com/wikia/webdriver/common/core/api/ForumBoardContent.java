package com.wikia.webdriver.common.core.api;

import com.wikia.webdriver.common.contentpatterns.URLsContent;
import com.wikia.webdriver.common.core.helpers.User;
import com.wikia.webdriver.common.core.url.UrlBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class ForumBoardContent extends ApiCall {
  private User user;
  private String title;
  private String content;

  public ForumBoardContent(User user, String title, String content) {
    this.user = user;
    this.title = String.format("%s:%s", URLsContent.FORUM_BOARD_NAMESPACE, title);
    this.content = content;
  }

  @Override
  protected User getUser() {
    return user;
  }

  @Override
  protected String getURL() {
    return UrlBuilder.createUrlBuilder().getUrl()
           + "/wikia.php?controller=ForumExternal&method=createNewBoard&format=json";
  }

  @Override
  protected ArrayList<BasicNameValuePair> getParams() {
    ArrayList<BasicNameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("boardTitle", title));
    params.add(new BasicNameValuePair("boardDescription", content));
    params.add(new BasicNameValuePair("token", new EditToken(user).getEditToken()));
    return params;
  }
}
