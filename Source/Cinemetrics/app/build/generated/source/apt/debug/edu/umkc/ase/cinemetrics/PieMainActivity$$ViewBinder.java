// Generated code from Butter Knife. Do not modify!
package edu.umkc.ase.cinemetrics;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PieMainActivity$$ViewBinder<T extends edu.umkc.ase.cinemetrics.PieMainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689613, "field 'navigationView'");
    target.navigationView = finder.castView(view, 2131689613, "field 'navigationView'");
    view = finder.findRequiredView(source, 2131689646, "field 'drawerLayout'");
    target.drawerLayout = finder.castView(view, 2131689646, "field 'drawerLayout'");
  }

  @Override public void unbind(T target) {
    target.navigationView = null;
    target.drawerLayout = null;
  }
}
