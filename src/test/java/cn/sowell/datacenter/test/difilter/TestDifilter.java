package cn.sowell.datacenter.test.difilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.junit.Test;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.antlr.AntlrUtils;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterLexer;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterParser;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterVisitorImpl;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;

public class TestDifilter {
	
	@Resource
	DictionaryService dictService;
	
	
	private static String prepareFileName = "d://prepare.dat";
	
	public void prepareFields() {
		List<DictionaryField> fields = dictService.getAllFields("UWfKlUu5fn");
        File file =new File(prepareFileName);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(fields);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }
	}
	
	public static Object readPrepareFields()
    {
        Object temp=null;
        File file =new File(prepareFileName);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            temp=objIn.readObject();
            objIn.close();
            System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }
	
	@Test
	public void filterField() throws Exception{
		@SuppressWarnings("unchecked")
		List<DictionaryField> fields = (List<DictionaryField>) readPrepareFields();
		
		Map<Long, DictionaryField> fieldMap = CollectionUtils.toMap(fields, DictionaryField::getId);
		
		DifilterVisitorImpl visitor = new DifilterVisitorImpl(fieldMap);
		Supplier<Set<DictionaryField>> supplier = AntlrUtils.getPreparedSupplier(
				visitor, 
				"d://TestDictFilter", 
				DifilterLexer::new, 
				DifilterParser::new,
				DifilterParser::progs);
		System.out.println();
		System.out.println(renderIds(visitor.getCurrentRange()));
		supplier.get();
		System.out.println();
		System.out.println(renderIds(visitor.getCurrentRange()));
	}

	private List<String> renderIds(Set<DictionaryField> currentRange) {
		SortedSet<DictionaryField> sorted = new TreeSet<>((o1, o2)->o1.getId().compareTo(o2.getId()));
		sorted.addAll(currentRange);
		return CollectionUtils.toList(sorted, (field)->{
			return field.getId() + "-" + field.getTitle() + "-" + field.getType() + "\r\n";
		});
	}
	
	
}
