package tag;

public enum MyPageBody {
    MY_PAGE_BODY;

    public static String renderMyPage(String readFile, String profileImagePath){
        return readFile.replace("${profileImage}", "src=\"" + profileImagePath + "\"");
    }
}
