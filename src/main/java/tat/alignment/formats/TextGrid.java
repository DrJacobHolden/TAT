package tat.alignment.formats;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kalda on 8/04/2016.
 */
public class TextGrid implements AlignmentFormat {

    public static final String FILE_EXTENSION = ".textgrid";
    private final Pattern XMIN_PATTERN = Pattern.compile("[\\s]*xmin[\\s]*=[\\s]*([0-9]+(.[0-9]+)?)");
    private final Pattern XMAX_PATTERN = Pattern.compile("[\\s]*xmax[\\s]*=[\\s]*([0-9]+(.[0-9]+)?)");
    private final Pattern TEXT_PATTERN = Pattern.compile("[\\s]*text[\\s]*=[\\s]*\"([^\"]*)\"");
    private final Pattern INTERVAL_SIZE = Pattern.compile("[\\s]*intervals:[\\s]*size[\\s]*=[\\s]*([0-9]+)");
    private final Pattern TIER_NAME = Pattern.compile("[\\s]*name[\\s]*=[\\s]*\"([^\"]*)\"");
    private String content;
    private List<Interval> words = new ArrayList<>();
    private List<Interval> canonicalPhones = new ArrayList<>();
    private List<Interval> phones = new ArrayList<>();

    public TextGrid(String content) {
        this.content = content;
    }

    public static TextGrid load(InputStream inputStream) throws IOException, ParseException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, Charset.defaultCharset());
        TextGrid textGrid = new TextGrid(writer.toString());

        BufferedReader in = new BufferedReader(new InputStreamReader(textGrid.getStream()));
        String line = null;

        int lineNum = 0;
        while ((line = in.readLine()) != null) {
            lineNum++;
            if (lineNum == 7) {
                textGrid.buildItems(in);
            }
        }

        in.close();
        return textGrid;
    }

    private void buildItems(BufferedReader in) throws IOException, ParseException {
        //item []:
        in.readLine();
        //Build all tiers
        buildTier(in);
        //buildTier(in);
    }

    private void buildTier(BufferedReader in) throws IOException, ParseException {
        //item [1]:
        in.readLine();
        //class = "IntervalTier"
        in.readLine();

        //name = "Mary"
        Matcher tierNameMatcher = TIER_NAME.matcher(in.readLine());
        tierNameMatcher.find();
        String tierName = tierNameMatcher.group(1);

        List<Interval> intervalList;
        if (tierName.equals("ORT")) {
            intervalList = words;
        } else if (tierName.equals("KAN")) {
            intervalList = canonicalPhones;
        } else if (tierName.equals("MAU")) {
            intervalList = phones;
        } else {
            //Process it anyway, even though we'll ditch it
            intervalList = new ArrayList<>();
        }

        //xmin = 0
        in.readLine();
        //xmax = 2.3
        in.readLine();
        //intervals: size = 1
        Matcher intervalSizeMatcher = INTERVAL_SIZE.matcher(in.readLine());
        intervalSizeMatcher.find();
        int intervalLength = Integer.parseInt(intervalSizeMatcher.group(1));

        for (int i = 0; i < intervalLength; i++) {
            intervalList.add(buildInterval(in));
        }
    }

    private Interval buildInterval(BufferedReader in) throws IOException, ParseException {
        Interval interval = new Interval();

        //intervals [1]:
        in.readLine();

        Matcher startTimeMatcher = XMIN_PATTERN.matcher(in.readLine());
        startTimeMatcher.find();
        interval.start_time = Float.parseFloat(startTimeMatcher.group(1));

        Matcher endTimeMatcher = XMAX_PATTERN.matcher(in.readLine());
        endTimeMatcher.find();
        interval.end_time = Float.parseFloat(endTimeMatcher.group(1));

        Matcher textMatcher = TEXT_PATTERN.matcher(in.readLine());
        textMatcher.find();
        interval.text = textMatcher.group(1);

        return interval;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(content.getBytes(Charset.defaultCharset()));
    }

    public class Interval {
        private float start_time;
        private float end_time;
        private String text;
    }
}
