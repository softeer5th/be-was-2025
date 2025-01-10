package enums;

import exception.ClientErrorException;

import static exception.ErrorCode.UNSUPPORTED_HTTP_VERSION;

public enum HttpVersion {
    HTTP1(1, 1);

    private final int majorVersion;
    private final int minorVersion;

    HttpVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }


    public static HttpVersion matchOrElseThrow(String versionString) {
        final MajorAndMinor result = getMajorAndMinor(versionString);

        for (HttpVersion version : HttpVersion.values()) {
            if (version.majorVersion == result.major() && version.minorVersion == result.minor())
                return version;
        }

        throw new ClientErrorException(UNSUPPORTED_HTTP_VERSION);
    }

    private static MajorAndMinor getMajorAndMinor(String versionString) {
        final String[] majorAndMinor = versionString.split("/")[1].split("\\.");

        int major = Integer.parseInt(majorAndMinor[0]);
        int minor = 0;

        if (majorAndMinor.length == 2) {
            minor = Integer.parseInt(majorAndMinor[1]);
        }
        return new MajorAndMinor(major, minor);
    }

    private record MajorAndMinor(int major, int minor) {
    }
}
