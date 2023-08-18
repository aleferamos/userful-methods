import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MethodsUtils {
    public void deleteElementsThatWasDeleteOfList(
            List<?> elementsComingDatabase,
            List<?> elementsToSendToDatabase,
            Object serviceClassToExecuteDelete
    ) {
        List<Integer> elementsIdToDelete = new ArrayList<>();

        List<Integer> idListElementsFound = elementsComingDatabase
                .stream()
                .map(item -> {
                    Class<?> clazz = item.getClass();
                    Field field;

                    try {
                        field = clazz.getDeclaredField("id");
                        field.setAccessible(true);

                        return (Integer) field.get(item);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        List<Integer> comingElementsId = elementsToSendToDatabase
                .stream()
                .map(item -> {
                    Class<?> clazz = item.getClass();
                    Field field;

                    try {
                        field = clazz.getDeclaredField("id");
                        field.setAccessible(true);

                        return (Integer) field.get(item);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        idListElementsFound.forEach(itemFound -> {
            if (!comingElementsId.contains(itemFound)) {
                elementsIdToDelete.add(itemFound);
            }
        });

        try {
            Class<?> classeDeServico = serviceClassToExecuteDelete.getClass();
            Method metodoDeleteAllById = classeDeServico.getMethod("deleteAllById", List.class);
            metodoDeleteAllById.invoke(serviceClassToExecuteDelete, elementsIdToDelete);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
