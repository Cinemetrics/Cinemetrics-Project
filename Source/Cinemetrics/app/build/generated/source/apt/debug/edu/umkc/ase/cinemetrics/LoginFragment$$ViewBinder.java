// Generated code from Butter Knife. Do not modify!
package edu.umkc.ase.cinemetrics;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginFragment$$ViewBinder<T extends edu.umkc.ase.cinemetrics.LoginFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689741, "field 'progressBar'");
    target.progressBar = finder.castView(view, 2131689741, "field 'progressBar'");
    view = finder.findRequiredView(source, 2131689739, "field 'view'");
    target.view = finder.castView(view, 2131689739, "field 'view'");
    view = finder.findRequiredView(source, 2131689747, "method 'loginwithGoogle'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.loginwithGoogle(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689746, "method 'loginwithFacebook'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.loginwithFacebook(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689748, "method 'loginwithTwitter'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.loginwithTwitter(p0);
        }
      });
    view = finder.findRequiredView(source, 2131689742, "method 'getStarted'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.getStarted(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.progressBar = null;
    target.view = null;
  }
}
