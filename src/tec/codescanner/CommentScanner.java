package tec.codescanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CommentScanner {

    private String code = "";

    public void removeComments(String s) {
        String[] code = s.split("\n");
        ArrayList<String> newCode = new ArrayList<>();
        int inComment = 0;
        for (String st : code) {
            if (st.startsWith("/*")) {
                inComment++;
            }
            if (!(st.startsWith("//")) && inComment == 0) {
                newCode.add(st);
            }
            if (st.endsWith("*/")) {
                inComment--;
            }
            if (inComment < 0) {
                inComment = 0;
            }

        }
        code = new String[newCode.size()];
        for (int i = 0; i < newCode.size(); i++) {
            code[i] = newCode.get(i);
        }
        this.code = Arrays.stream(code).collect(Collectors.joining("\n"));
    }

    public String getCode() {
        return code;
    }

}
