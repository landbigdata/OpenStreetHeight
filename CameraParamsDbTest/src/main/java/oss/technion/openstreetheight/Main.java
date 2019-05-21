package oss.technion.openstreetheight;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // Relevant for 31.10.2018
    public static void main(String... args) throws Exception {

            /*
            Observable.just(
                    Jsoup.connect(
                            String.format("https://www.phonearena.com/phones/OS/Android/page/%d", i)
                    )
                            .get()
            )
                    .flatMapIterable(doc -> doc.select("div#phones div.s_listing div"))
                    .map(phone -> phone.selectFirst("a.s_thumb"))
                    .map(linkElem -> linkElem.attr("href"))

                    // now we start parsing phone page itself
                    .map(link ->
                            Jsoup.connect(
                                    String.format("https://www.phonearena.com/phones/%s/", link)
                            )
                                    .get()
                    )
                    .map(doc -> doc.select("div#camera_cPoint li"))
                    .subscribe(li -> {

                    })
*/
        Document phone = getPhonePage("Xiaomi Mi Mix 3");

        // in mm
        String focalLength = Observable
                .just(phone.select("table.model-information-table.row-selection").get(10))
                .flatMapIterable(table -> table.select("tr"))
                .filter(tr -> tr.select("td").get(0).text().contains("Focal length"))
                .map(tr -> tr.select("td").get(1))
                .map(Element::text)
                .map(focalLengthText -> focalLengthText.substring(0, 4).trim())
                .blockingSingle("null");

        // in micro-meters
        String pixelSize = Observable
                .just(phone.select("table.model-information-table.row-selection").get(10))
                .flatMapIterable(table -> table.select("tr"))
                .filter(tr -> tr.select("td").get(0).text().contains("Pixel size"))
                .map(tr -> tr.select("td").get(1))
                .map(Element::text)
                .map(pixelSizeText -> pixelSizeText.substring(0, 4).trim())
                .blockingSingle("null");


        // Pixel size can be null - it can reside in section w/o title
        if (pixelSize.equals("null")) {
            pixelSize = Observable
                    .just(phone.select("table.model-information-table.row-selection").get(10))
                    .flatMapIterable(table -> table.select("tr"))
                    .filter(tr -> tr.select("td").get(0).text().isEmpty())
                    .map(tr -> tr.select("td").get(1))
                    .map(Element::text)
                    .map(pixelSizeText -> {
                        // Run regex for 1-st time
                        Matcher m = Pattern.compile("Pixel size - (\\d+\\.\\d+) μm").matcher(pixelSizeText);
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

        System.out.println(pixelSize);



    }

    private static Document getPhonePage(String deviceId) throws Exception {
        return Observable.just(
                Jsoup.connect(
                        String.format("https://www.devicespecifications.com/index.php?action=search&language=en&search=%s", deviceId)
                )
                        .get().body().text()
        )
                .map(text -> new JsonParser().parse(text))
                .map(JsonElement::getAsJsonArray)
                .map(array -> array.get(0))
                .map(JsonElement::getAsJsonObject)
                .map(obj -> obj.get("url"))
                .map(JsonElement::getAsString)
                .map(url -> Jsoup.connect(url).get())
                .blockingSingle();
    }
}
