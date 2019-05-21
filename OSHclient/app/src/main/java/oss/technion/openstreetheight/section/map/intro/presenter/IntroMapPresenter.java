package oss.technion.openstreetheight.section.map.intro.presenter;


import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.map.intro.view.IntroMapView;

public class IntroMapPresenter {
    private IntroMapView view;

    public IntroMapPresenter(IntroMapView view) {
        this.view = view;
    }

    public void onStart() {
        view.showBackButton(Router.isShowBackButton);
    }

    public void onPickCornerButtonClick() {
        Router.onPickSidesClickInIntroMap();
    }
}
