package oss.technion.openstreetheight.mvp;


public interface SectionSwitcher {

    // Order of this enum IS important for backstack
    enum Section {AUTO_CAM_PARAMS, MANUAL_CAM_PARAMS, OSM_AUTH, OPTIONS, INTRO_MAP, MAP, PHOTO, DRAW, FINISH}

    void doTransaction(Section dest);

    void finish();
}
