package enums;

import exception.ClientErrorException;

import java.util.List;

import static exception.ErrorCode.UNSUPPORTED_HTTP_VERSION;

/**
 * HTTP 버전 정보를 나타내는 열거형 클래스입니다.
 * 각 HTTP 버전은 주요 및 부가 버전으로 구성됩니다.
 */
public enum HttpVersion {
    /**
     * HTTP/1.1 버전
     */
    HTTP1_1(1, 1),

    /**
     * HTTP/2 버전
     */
    HTTP2(2, 0),

    /**
     * HTTP/3 버전
     */
    HTTP3(3, 0);

    private final int majorVersion;
    private final int minorVersion;

    /**
     * HttpVersion 생성자
     *
     * @param majorVersion 주요 버전
     * @param minorVersion 부가 버전
     */
    HttpVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    /**
     * 주어진 버전 문자열과 지원되는 HTTP 버전 목록을 바탕으로 일치하는 HTTP 버전을 찾습니다.
     * 만약 일치하는 버전이 없으면 {@link ClientErrorException} 예외를 던집니다.
     *
     * @param versionString   확인할 HTTP 버전 문자열 (예: "HTTP/1.1")
     * @param supportedVersions 지원되는 HTTP 버전 목록
     * @return 일치하는 HTTP 버전
     * @throws ClientErrorException 지원되지 않는 HTTP 버전인 경우
     */
    public static HttpVersion matchOrElseThrow(String versionString, List<HttpVersion> supportedVersions) {
        final MajorAndMinor result = getMajorAndMinor(versionString);

        for (HttpVersion version : HttpVersion.values()) {
            if (supportedVersions.stream().anyMatch(v -> v == version)
                    && version.majorVersion == result.major()
                    && version.minorVersion == result.minor())
                return version;
        }

        throw new ClientErrorException(UNSUPPORTED_HTTP_VERSION);
    }

    /**
     * 주어진 버전 문자열에서 주요 및 부가 버전을 추출합니다.
     *
     * @param versionString HTTP 버전 문자열 (예: "HTTP/1.1")
     * @return MajorAndMinor 객체 (주요 버전과 부가 버전)
     */
    private static MajorAndMinor getMajorAndMinor(String versionString) {
        final String[] majorAndMinor = versionString.split("/")[1].split("\\.");

        int major = Integer.parseInt(majorAndMinor[0]);
        int minor = 0;

        if (majorAndMinor.length == 2) {
            minor = Integer.parseInt(majorAndMinor[1]);
        }
        return new MajorAndMinor(major, minor);
    }

    /**
     * 주요 및 부가 버전을 나타내는 레코드 클래스입니다.
     */
    private record MajorAndMinor(int major, int minor) {
    }
}
