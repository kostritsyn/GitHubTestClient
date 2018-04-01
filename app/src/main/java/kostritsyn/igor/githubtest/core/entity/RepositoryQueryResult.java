package kostritsyn.igor.githubtest.core.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TypeConverters(RepositoryQueryResult.RepositoryIdsTypeConverter.class)
@Entity
public class RepositoryQueryResult {

    @NonNull
    @PrimaryKey
    public final String query;
    public final List<Integer> repositoryIds;
    public final int totalCount;
    @Nullable
    public final Integer next;

    public RepositoryQueryResult(@NonNull String query, List<Integer> repositoryIds, int totalCount,
                                 @Nullable Integer next) {
        this.query = query;
        this.repositoryIds = repositoryIds;
        this.totalCount = totalCount;
        this.next = next;
    }

    public static class RepositoryIdsTypeConverter {

        @TypeConverter
        public static List<Integer> stringToIntList(String data) {
            if (data == null || data.isEmpty()) {
                return Collections.emptyList();
            }

            String[] parts = data.split(",");
            List<Integer> result = new ArrayList<>(parts.length);
            for (String part : parts) {
                result.add(Integer.parseInt(part));
            }

            return result;
        }

        @TypeConverter
        public static String intListToString(List<Integer> data) {

            if (data == null) {
                return null;
            }

            if (data.isEmpty()) {
                return "";
            }

            StringBuilder stringBuilder = new StringBuilder();
            int size = data.size();
            for (int i = 0; i < size; ++i) {

                stringBuilder.append(data.get(i));
                if (i != size - 1) {
                    stringBuilder.append(",");
                }
            }
            return stringBuilder.toString();
        }
    }
}
