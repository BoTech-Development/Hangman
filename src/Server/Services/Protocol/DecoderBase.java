package Server.Services.Protocol;

public class DecoderBase
{
    void RemoveLineBreakFromLines(String[] lines){
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replaceAll("\\r?\\n", "");
        }
    }
}
