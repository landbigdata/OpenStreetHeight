package oss.technion.openstreetheight.mvp;


public interface SectionSwitcher {

    enum Section {INTRO_MAP, MAP, PHOTO, DRAW, FINISH}

    void doTransaction(Section dest);

    void finish();
}
