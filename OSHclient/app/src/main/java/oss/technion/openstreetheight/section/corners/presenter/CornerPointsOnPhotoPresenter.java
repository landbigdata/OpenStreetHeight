package oss.technion.openstreetheight.section.corners.presenter;

import oss.technion.openstreetheight.R;
import oss.technion.openstreetheight.hub.MessageHub;
import oss.technion.openstreetheight.hub.messages.PhotoCornerPointsMsg;
import oss.technion.openstreetheight.hub.messages.PhotoMsg;
import oss.technion.openstreetheight.mvp.Router;
import oss.technion.openstreetheight.section.corners.view.CornerPointsOnPhotoView;

public class CornerPointsOnPhotoPresenter {
    // Fields
    private CornerPointsOnPhotoView view;

    // Constructors
    public CornerPointsOnPhotoPresenter(CornerPointsOnPhotoView view) {
        this.view = view;
    }

    // Methods
    public void onStart() {
        PhotoMsg photoMsg = MessageHub.get(PhotoMsg.class);

        view.setActionBarTitle(R.string.photo_pick_six_corner_points);
        view.showBackButton(Router.isShowBackButton);

        view.setBackground(photoMsg.path);

    }


    public void onFinishButtonClick() {
        double[][] cornerPointsArray = new double[2][6];
        view.fillCornerPointsArray(cornerPointsArray);

        PhotoCornerPointsMsg msg = new PhotoCornerPointsMsg(cornerPointsArray);
        MessageHub.put(PhotoCornerPointsMsg.class, msg);

        Router.onFinishClickInPhotoCorners();
    }
}