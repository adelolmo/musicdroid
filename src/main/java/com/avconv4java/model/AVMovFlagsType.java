package com.avconv4java.model;

/**
 * @author Vladislav Bauer
 */

public enum AVMovFlagsType {

    /**
     * Start a new fragment at each video keyframe.
     */
    FRAG_KEY_FRAME(Constants.FRAG_KEY_FRAME),

    /**
     * Allow the caller to manually choose when to cut fragments, by calling av_write_frame(ctx, NULL)
     * to write a fragment with the packets written so far. (This is only useful with other applications
     * integrating libavformat, not from avconv.)
     */
    FRAG_CUSTOM(Constants.FRAG_CUSTOM),

    /**
     * Write an initial moov atom directly at the start of the file, without describing any samples in it.
     * Generally, an mdat/moov pair is written at the start of the file, as a normal MOV/MP4 file, containing only
     * a short portion of the file. With this option set, there is no initial mdat atom, and the moov atom only
     * describes the tracks but has a zero duration.
     * <p>
     * Files written with this option set do not work in QuickTime.
     * This option is implicitly set when writing ismv (Smooth Streaming) files.
     */
    EMPTY_MOOV(Constants.EMPTY_MOOV),

    /**
     * Write a separate moof (movie fragment) atom for each track. Normally, packets for all tracks are written
     * in a moof atom (which is slightly more efficient), but with this option set, the muxer writes one moof/mdat
     * pair for each track, making it easier to separate tracks.
     * <p>
     * This option is implicitly set when writing ismv (Smooth Streaming) files.
     */
    SEPARATE_MOOF(Constants.SEPARATE_MOOF),

    /**
     * Run a second pass moving the index (moov atom) to the beginning of the file.
     * This operation can take a while, and will not work in various situations such as fragmented output,
     * thus it is not enabled by default.
     */
    FAST_START(Constants.FAST_START);


    private final String name;


    private AVMovFlagsType(final String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public static AVMovFlagsType findByName(final String name) {
        for (final AVMovFlagsType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }


    /**
     * @author Vladislav Bauer
     */

    public static final class Constants {

        public static final String FRAG_KEY_FRAME = "frag_keyframe";
        public static final String FRAG_CUSTOM = "frag_custom";
        public static final String EMPTY_MOOV = "empty_moov";
        public static final String SEPARATE_MOOF = "separate_moof";
        public static final String FAST_START = "faststart";


        private Constants() {
            throw new UnsupportedOperationException();
        }

    }

}
