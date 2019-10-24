package oss.technion.openstreetheight.model.camera.params;


import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LensFetcher {
    public static class LensTuple {
        public final float focalLenght; // in m
        public final float pixelSize; // in m

        public LensTuple(float focalLenght, float pixelSize) {
            this.focalLenght = focalLenght;
            this.pixelSize = pixelSize;
        }

        public boolean isEmpty() {
            return focalLenght == -1 && pixelSize == -1;
        }
    }


    public static Single<LensTuple> getLensParams(String buildModel) {
        return Single.fromCallable(() -> getLensParamsInner(buildModel))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Parsing
    private static LensTuple getLensParamsInner(String buildModel) {

        LensTuple resultTuple = new LensTuple(-1, -1);

        String possiblePhonesStr = Rx2AndroidNetworking
                .get("https://www.devicespecifications.com/index.php")
                .addQueryParameter("action", "search")
                .addQueryParameter("language", "en")
                .addQueryParameter("search", buildModel)
                .build()
                // Now we process output
                .getStringSingle()
                .blockingGet();

        if (possiblePhonesStr.equals("0")) {
            return new LensTuple(-1, -1);
        }


        JSONArray possiblePhones = null;
        try {
            possiblePhones = new JSONArray(possiblePhonesStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < possiblePhones.length(); i++) {
            final int j = i;

            Document phoneDoc = Single
                    .just(possiblePhones)
                    .map(array -> array.getJSONObject(j))
                    .map(obj -> obj.getString("url"))
                    .map(url -> Jsoup.connect(url).get())
                    .blockingGet();

            Element paramsDiv = phoneDoc.selectFirst("div#main").child(5);


            if (paramsDiv.select("table").first().text().contains(buildModel)) {
                resultTuple = getParamTupleFromDoc(phoneDoc);
                if (!resultTuple.isEmpty()) break;

            } else{
                break;
            }

        }


        return resultTuple;
    }

    // Parsing
    private static LensTuple getParamTupleFromDoc(Document phone) {
        // First, we need to find location of "Primary Camera" section
        int primaryCameraSectionPos = 0;

        Element paramsDiv = phone.selectFirst("div#main").child(5);

        for (int i = 0; i < paramsDiv.children().size(); i++) {
            Element elem = paramsDiv.children().get(i);

            if (elem.is("header")) {
                String title = elem.selectFirst("h2.header").text();

                if (title.equals("Primary camera")) {
                    primaryCameraSectionPos = i + 1;
                    break;
                }
            }
        }


        // in mm
        String focalLengthMm = Observable
                .just(paramsDiv.child(primaryCameraSectionPos))
                .flatMapIterable(table -> table.select("tr"))
                .filter(tr -> tr.select("td").get(0).text().contains("Focal length"))
                .map(tr -> tr.select("td").get(1))
                .map(Element::text)
                .map(focalLengthText -> {
                    Matcher m = Pattern.compile("(\\d+\\.\\d+) mm").matcher(focalLengthText);
                    m.find();
                    String bigMatch = m.group();
                    //Run regex for 2-nd time
                    Matcher m2 = Pattern.compile("(\\d+\\.\\d+)").matcher(bigMatch);
                    m2.find();
                    String smallMatch = m2.group();
                    return smallMatch;
                })
                .blockingSingle("null");

        // in micro-meters
        String pixelSizeMicroM = Observable
                .just(paramsDiv.child(primaryCameraSectionPos))
                .flatMapIterable(table -> table.select("tr"))
                .filter(tr -> tr.select("td").get(0).text().contains("Pixel size"))
                .map(tr -> tr.select("td").get(1))
                .map(Element::text)
                .map(pixelSizeText -> {
                    Matcher m = Pattern.compile("(\\d+\\.\\d+) \\u00b5m").matcher(pixelSizeText);
                    m.find();
                    String bigMatch = m.group();
                    //Run regex for 2-nd time
                    Matcher m2 = Pattern.compile("(\\d+\\.\\d+)").matcher(bigMatch);
                    m2.find();
                    String smallMatch = m2.group();
                    return smallMatch;
                })
                .blockingSingle("null");


        // Pixel size can be null - it can reside in section w/o title
        if (pixelSizeMicroM.equals("null")) {
            pixelSizeMicroM = Observable
                    .just(paramsDiv.child(primaryCameraSectionPos))
                    .flatMapIterable(table -> table.select("tr"))
                    .filter(tr -> tr.select("td").get(0).text().isEmpty())
                    .map(tr -> tr.select("td").get(1))
                    .map(Element::text)
                    .filter(t -> t.contains("Pixel size"))
                    .map(pixelSizeText -> {
                        // Run regex for 1-st time
                        Matcher m = Pattern.compile("Pixel size - (\\d+\\.\\d+) \\u00b5m").matcher(pixelSizeText);
                        m.find();
                        String bigMatch = m.group();
                        //Run regex for 2-nd time
                        Matcher m2 = Pattern.compile("(\\d+\\.\\d+)").matcher(bigMatch);
                        m2.find();
                        String smallMatch = m2.group();
                        return smallMatch;
                    })
                    .blockingSingle("null");
        }


        float focalLength = focalLengthMm.equals("null") ? -1 : Float.valueOf(focalLengthMm) * 1e-3f;
        float pixelSize = pixelSizeMicroM.equals("null") ? -1 : Float.valueOf(pixelSizeMicroM) * 1e-6f;

        LensTuple tuple = new LensTuple(focalLength, pixelSize);

        return tuple;
    }


}