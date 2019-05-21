package oss.technion.openstreetheight.mvp;

import android.os.Bundle;

import oss.technion.openstreetheight.model.camera.params.CameraParams;
import oss.technion.openstreetheight.mvp.SectionSwitcher.Section;

public class Router {

    private static SectionSwitcher switcher;

    private static Section curSection;

    public static boolean isShowBackButton;

    /*
     * Should be called from onCreate() of main activity
     */
    public static void setSwitcher(SectionSwitcher switcher) {
        Router.switcher = switcher;
    }

    public static void onInitialSwitch() {
        isShowBackButton = false;
        switcher.doTransaction(Section.INTRO_MAP);
        curSection = Section.INTRO_MAP;
    }

    /*
     * May be called from Main Activity
     */
    public static void onCameraDialogExitButtonClick() {
        switcher.finish();
    }

    /*
     * First step
     * Called by IntroMapPresenter
     */
    public static void onPickSidesClickInIntroMap() {
        isShowBackButton = true;
        switcher.doTransaction(Section.MAP);
        curSection = Section.MAP;
    }

    /*
     * Second step
     * Called by BuildingSidesMapPresenter
     */
    public static void onContinueClickInBuildingSidesMap() {
        isShowBackButton = true;
        switcher.doTransaction(Section.PHOTO);
        curSection = Section.PHOTO;
    }

    /*
     * Third step
     * Called by PhotoFragment
     */
    public static void onPhotoShotInPhoto() {
        isShowBackButton = true;
        switcher.doTransaction(Section.DRAW);
        curSection = Section.DRAW;
    }

    /*
     * Fourth step
     * Called by PhotoCorners
     */
    public static void onFinishClickInPhotoCorners() {
        isShowBackButton = true;
        switcher.doTransaction(Section.FINISH);
        curSection = Section.FINISH;
    }

    public static void onRepeatClickInFinish() {
        onInitialSwitch();
    }


    // Due to framework requirements, we do not issue
    public static void onBackClick() {
        if (curSection == Section.INTRO_MAP) {
            switcher.finish();
        } else {
            curSection = Section.values()[curSection.ordinal() - 1];
            isShowBackButton = (curSection != Section.INTRO_MAP);

            switcher.doTransaction(curSection);
        }
    }


    private static final String CUR_SECTION_BUNDLE_KEY = "curSection";
    private static final String SHOW_BACK_BUTTON_KEY = "showBackButton";

    public static void saveState(Bundle state) {
        state.putSerializable(CUR_SECTION_BUNDLE_KEY, curSection);
        state.putBoolean(SHOW_BACK_BUTTON_KEY, isShowBackButton);
    }

    @SuppressWarnings("unchecked")
    public static void restoreState(Bundle state) {
        curSection = (Section) state.getSerializable(CUR_SECTION_BUNDLE_KEY);
        isShowBackButton = state.getBoolean(SHOW_BACK_BUTTON_KEY);
    }

}
