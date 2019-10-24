package oss.technion.openstreetheight.mvp;

import android.os.Bundle;

import java.io.Serializable;
import java.util.Stack;

import oss.technion.openstreetheight.model.FirstLaunch;
import oss.technion.openstreetheight.mvp.SectionSwitcher.Section;

public class Router {

    private static final class BackItem implements Serializable {
        public boolean isShowBackButton;
        public Section section;

        public BackItem(Section section, boolean isShowBackButton) {
            this.isShowBackButton = isShowBackButton;
            this.section = section;
        }
    }

    private static final String CUR_SECTION_BUNDLE_KEY = "curSectionKey";
    private static final String SHOW_BACK_BUTTON_KEY = "showBackButtonKey";
    private static final String BACK_STACK_KEY = "backStackKey";

    public static boolean isShowBackButton;

    private static SectionSwitcher switcher;
    private static Section curSection;

    private static Stack<BackItem> backStack = new Stack<>();


    private static void doTransactionUp(Section sect, boolean isShowBackButton) {
        // Add previous item to backstack
        if (Router.curSection != null) {
            backStack.push(new BackItem(Router.curSection, Router.isShowBackButton));
        }

        Router.curSection = sect;
        Router.isShowBackButton = isShowBackButton;


        // Now actually do transaction
        switcher.doTransaction(sect);

    }

    private static void doTransactionDown(Section sect, boolean isShowBackButton) {
        // Do not save current section in back stack

        Router.curSection = sect;
        Router.isShowBackButton = isShowBackButton;


        // Now actually do transaction
        switcher.doTransaction(sect);

    }


    /*
     * Should be called from onCreate() of main activity
     */
    public static void setSwitcher(SectionSwitcher switcher) {
        Router.switcher = switcher;
    }

    // Responsible for actions in Options fragment
    public static class Options {
        public static void onCamParamsClick() {
            doTransactionUp(Section.MANUAL_CAM_PARAMS, true);
        }

        public static void onOsmAuthClick() {
            doTransactionUp(Section.OSM_AUTH, true);
        }

        public static void onMeasureClick() {
            doTransactionUp(Section.INTRO_MAP, true);
        }
    }

    /*
     * ENTRY POINT
     *
     * -> AUTO_CAM_PARAMS || -> OPTIONS
     */
    public static void onInitialSwitch() {
        // We need to make decision whether we are showing auto param search or directly first step


        if (FirstLaunch.isInCamParamSetMode) {
            // Launch auto cam params
            doTransactionUp(Section.AUTO_CAM_PARAMS, false);
        } else {
            // Start with options
            doTransactionUp(Section.OPTIONS, false);
        }

    }


    /*
     * AUTO_CAM_PARAMS -> MANUAL_CAM_PARAMS
     */
    public static void onAutoCamParamsContinue() {
        doTransactionUp(Section.MANUAL_CAM_PARAMS, false);
    }

    /*
     * ManualCamParams -> OsmAuth || ManualCamParams -> Options
     */
    public static void onManualCamParamsDone() {
        if (FirstLaunch.isInCamParamSetMode) {
            // In InitialSwitch we started with CamParams
            // Now we proceed to OsmAuth
            doTransactionUp(Section.OSM_AUTH, true);
        } else {
            // It was called from Options, so we return to it
            doTransactionUp(Section.OPTIONS, false);
        }

    }

    /*
     * OSM_AUTH -> OPTIONS (either first launch or options click)
     * OSM_AUTH -> FINISH (when user was not authorized)
     */
    public static void onOsmAuthDoneClick() {
        if(FirstLaunch.isInCamParamSetMode) {
            FirstLaunch.isInCamParamSetMode = false;

            doTransactionUp(Section.OPTIONS, true);
        } else { // click from Options or from Finish
            onBackClick();
        }
    }

    /*
     * OSM_AUTH -> OPTIONS
     */
    public static void onOsmAuthSkipClick() {
        onOsmAuthDoneClick();
    }

    /*
     * Step 1
     * Called by IntroMapPresenter
     */
    public static void onPickSidesClickInIntroMap() {
        doTransactionUp(Section.MAP, true);
    }

    /*
     * Step 2
     * Called by BuildingSidesMapPresenter
     */
    public static void onContinueClickInBuildingSidesMap() {
        doTransactionUp(Section.PHOTO, true);
    }

    /*
     * Step 3
     * Called by PhotoFragment
     */
    public static void onPhotoShotInPhoto() {
        doTransactionUp(Section.DRAW, true);
    }

    /*
     * Step 4
     * Called by PhotoCorners
     */
    public static void onFinishClickInPhotoCorners() {
        doTransactionUp(Section.FINISH, true);
    }


    public static void finish_onRepeatClick() {
        backStack.clear();
        backStack.add(new BackItem(Section.OPTIONS, false));

        doTransactionDown(Section.INTRO_MAP, true);
    }

    public static void finish_OnShareHeightOsmNonAuthClick() {
        doTransactionUp(Section.OSM_AUTH, true);
    }


    // Due to framework requirements, we do not issue
    public static void onBackClick() {

        // We simply pop back stack
        if (backStack.isEmpty()) {
            switcher.finish();
        } else {
            BackItem back = backStack.pop();
            doTransactionDown(back.section, back.isShowBackButton);
        }

    }


    public static void saveState(Bundle state) {
        state.putSerializable(CUR_SECTION_BUNDLE_KEY, curSection);
        state.putBoolean(SHOW_BACK_BUTTON_KEY, isShowBackButton);
        state.putSerializable(BACK_STACK_KEY, backStack);
    }

    public static void restoreState(Bundle state) {
        curSection = (Section) state.getSerializable(CUR_SECTION_BUNDLE_KEY);
        isShowBackButton = state.getBoolean(SHOW_BACK_BUTTON_KEY);
        backStack = (Stack<BackItem>) state.getSerializable(BACK_STACK_KEY);
    }


}
