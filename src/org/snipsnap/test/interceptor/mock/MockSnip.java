package org.snipsnap.test.interceptor.mock;

import org.snipsnap.snip.*;
import snipsnap.api.snip.Snip;
import org.snipsnap.snip.attachment.Attachments;
import snipsnap.api.label.Labels;
import org.snipsnap.user.Permissions;
import snipsnap.api.user.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.List;

import gabriel.Principal;
import snipsnap.api.snip.*;

public abstract class MockSnip implements snipsnap.api.snip.Snip {
  public void setParentName(String name) {
  }

  public void setCommentedName(String name) {
  }

  public void handle(HttpServletRequest request) {
  }

  public String getLink() {
    return null;
  }

  public Principal getOwner() {
    return null;
  }

  public String getName() {
    return null;
  }

  public Writer appendTo(Writer s) throws IOException {
    return null;
  }

  public Access getAccess() {
    return null;
  }

  public boolean isOwner(User user) {
    return false;
  }

  public Modified getModified() {
    return null;
  }

  public boolean isWeblog() {
    return false;
  }

  public boolean isNotWeblog() {
    return false;
  }

  public void addPermission(String permission, String role) {
  }

  public void setPermissions(Permissions permissions) {
  }

  public Permissions getPermissions() {
    return null;
  }

  public String getOUser() {
    return null;
  }

  public void setOUser(User oUser) {
  }

  public void setOUser(String oUser) {
  }

  public Attachments getAttachments() {
    return null;
  }

  public void setAttachments(Attachments attachments) {
  }

  public snipsnap.api.label.Labels getLabels() {
    return null;
  }

  public void setLabels(Labels labels) {
  }

  public Links getBackLinks() {
    return null;
  }

  public Links getSnipLinks() {
    return null;
  }

  public void setBackLinks(Links backLinks) {
  }

  public void setSnipLinks(Links snipLinks) {
  }

  public int getViewCount() {
    return 0;
  }

  public void setViewCount(int count) {
  }

  public int incViewCount() {
    return 0;
  }

  public Timestamp getCTime() {
    return null;
  }

  public void setCTime(Timestamp cTime) {
  }

  public Timestamp getMTime() {
    return null;
  }

  public void setMTime(Timestamp mTime) {
  }

  public String getCUser() {
    return null;
  }

  public void setCUser(User cUser) {
  }

  public void setCUser(String cUser) {
  }

  public String getMUser() {
    return null;
  }

  public void setMUser(User mUser) {
  }

  public void setMUser(String mUser) {
  }

  public List getChildren() {
    return null;
  }

  public void setCommentedSnip(snipsnap.api.snip.Snip comment) {
  }

  public snipsnap.api.snip.Snip getCommentedSnip() {
    return null;
  }

  public boolean isComment() {
    return false;
  }

  public Comments getComments() {
    return null;
  }

  public List getChildrenDateOrder() {
    return null;
  }

  public List getChildrenModifiedOrder() {
    return null;
  }

  public void addSnip(snipsnap.api.snip.Snip snip) {
  }

  public void removeSnip(snipsnap.api.snip.Snip snip) {
  }

  public snipsnap.api.snip.Snip getParent() {
    return null;
  }

  public void setDirectParent(snipsnap.api.snip.Snip parentSnip) {
  }

  public void setParent(snipsnap.api.snip.Snip parentSnip) {
  }

  public String getShortName() {
    return null;
  }

  public String getNameEncoded() {
    return null;
  }

  public void setName(String name) {
  }

  public String getContent() {
    return null;
  }

  public void setContent(String content) {
  }

  public String getAttachmentString() {
    return null;
  }

  public String toXML() {
    return null;
  }

  public SnipPath getPath() throws IOException {
    return null;
  }

  public String getXMLContent() {
    return null;
  }

  public String getTitle() {
    return null;
  }
}
