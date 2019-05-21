package oss.technion.openstreetheight.section.corners.view;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

public interface CornerPointsOnPhotoView {
    void setBackground(String path);

    void setActionBarTitle(@StringRes int text);

    void fillCornerPointsArray(double[][] result);

    void showBackButton(boolean isShowBackButton);

}
