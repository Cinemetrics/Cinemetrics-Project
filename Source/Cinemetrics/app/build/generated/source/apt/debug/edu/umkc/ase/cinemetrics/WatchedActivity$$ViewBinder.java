// Generated code from Butter Knife. Do not modify!
package edu.umkc.ase.cinemetrics;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WatchedActivity$$ViewBinder<T extends edu.umkc.ase.cinemetrics.WatchedActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689646, "field 'navigationView'");
    target.navigationView = finder.castView(view, 2131689646, "field 'navigationView'");
    view = finder.findRequiredView(source, 2131689645, "field 'drawerLayout'");
    target.drawerLayout = finder.castView(view, 2131689645, "field 'drawerLayout'");
  }

  @Override public void unbind(T target) {
    target.navigationView = null;
    target.drawerLayout = null;
  }
}
