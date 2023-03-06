package backup.utils;

import java.util.Date;

/**
 * Classe para a formatação de Strings.
 * @author Leandro Aparecido de Almeida
 */
public final class StrUtils {
    
    /**
     * Formatar um número Long num múltiplo do byte (byte, KB, MB, GB, TB).
     * @param bytes número long a ser formatado.
     * @return String formatada.
     */
    public static String formatBytes(long bytes) { 
        double d;
        String m;
        if (bytes < 1024) {
            d = bytes;
            m = "B";
        } else if (bytes >= 1024 && bytes < 1048576) {
            d = bytes / 1024F;
            m = "KB";
        } else if (bytes >= 1048576 && bytes < 1073741824L) {
            d = bytes / 1048576F;
            m = "MB";
        } else if (bytes >= 1073741824L && bytes < 1099511627776L) {
            d = bytes / 1073741824F;
            m = "GB";
        } else {
            d = bytes / 1099511627776F;
            m = "TB";
        }
        String tam = String.format("%.2f", d) + " " + m;
        return tam;
    }
    
    /**
     * Formatar uma data para o formato "dd/mm/aaaa, hh:mm:ss".
     * @param date data a ser formatada.
     * @return String formatada.
     */
    public static String formatDate1(Date date) {
        return String.format("%1$td/%1$tm/%1$tY, %1$tH:%1$tM:%1$tS", date);
    }
    
    /**
     * Formatar uma data para o formato "dd/mm/aaaa, hh:mm".
     * @param date data a ser formatada.
     * @return String formatada.
     */
    public static String formatDate2(Date date) {
        return String.format("%1$td/%1$tm/%1$tY, %1$tH:%1$tM", date);
    }
    
    /**
     * Formatar uma data para o formato "dd/mm/aaaa".
     * @param date data a ser formatada.
     * @return String formatada.
     */
    public static String formatDate3(Date date) {
        return String.format("%1$td/%1$tm/%1$tY", date);
    }
    
    
    public static String formatHour(Date date) {
        return String.format("%1$tH:%1$tM:%1$tS", date);
    }
    
}