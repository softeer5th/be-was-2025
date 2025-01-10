package util;

import java.util.ArrayList;
import java.util.List;

public class PathUtils {

	/**
	 * @implSpec : 경로를 순차적으로 잘라서 가능한 경로들을 반환, 가장 구체적인 경로는 리스트 맨 앞에 위치
	 * @param path : 경로
	 * */

	// TODO: 경로 path를 받아서 가능한 여러 경로를 구하는 것이 좋은 방법일까? 경로가 맞지 않다면 에러를 반환하는 것이 맞지 않을까?
	public static List<String> generatePossiblePaths(String path) {
		String[] pathParts = path.split("/");
		List<String> possiblePaths = new ArrayList<>();

		for (int i = pathParts.length; i > 0; i--) {
			StringBuilder possiblePath = new StringBuilder("/");
			for (int j = 1; j < i; j++) {
				possiblePath.append(pathParts[j]).append("/");
			}
			// 생성된 경로 추가
			possiblePaths.add(possiblePath.toString());
		}

		return possiblePaths;
	}
}
