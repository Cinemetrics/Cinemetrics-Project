// Generated code from Butter Knife. Do not modify!
package edu.umkc.ase.cinemetrics;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginFragment$$ViewBinder<T extends edu.umkc.ase.cinemetrics.LoginFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755224, "field 'progressBar'");
    target.progressBar = finder.castView(view, 2131755224, "field 'progressBar'");
    view = finder.findRequiredView(source, 2131755222, "field 'view'");
    target.view = finder.castView(view, 2131755222, "field 'view'");
    view = finder.findRequiredView(source, 2131755231, "method 'loginwithGoogle'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.loginwithGoogle(p0);
        }
      });
    view = finder.findRequiredView(source, 2131755230, "method 'loginwithFacebook'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.loginwithFacebook(p0);
        }
      });
    view = finder.findRequiredView(source, 2131755232, "method 'loginwithTwitter'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.loginwithTwitter(p0);
        }
      });
    view = finder.findRequiredView(source, 2131755226, "method 'getStarted'");
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
