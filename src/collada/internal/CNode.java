package collada.internal;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class CNode {

	private String id;
	private String name;
	private String sid;
	private String type;
	private Vector3f translate;
	private List<Vector4f> rotate;
	private Vector3f scale;
	private List<CNode> children;
	private CInstanceController instanceController;
	private CInstanceGeometry instanceGeometry;
	
	public CNode(String id, String name, String sid, String type, 
			Vector3f translate, List<Vector4f> rotate, Vector3f scale, 
			List<CNode> children, CInstanceController cInstanceController, CInstanceGeometry cInstanceGeometry) {
		this.id = id;
		this.name = name;
		this.sid = sid;
		this.type = type;
		this.translate = translate;
		this.rotate = rotate;
		this.scale = scale;
		this.children = children;
		this.instanceController = cInstanceController;
		this.instanceGeometry = cInstanceGeometry;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSid() {
		return sid;
	}

	public String getType() {
		return type;
	}

	public Vector3f getTranslate() {
		return translate;
	}

	public List<Vector4f> getRotate() {
		return rotate;
	}

	public Vector3f getScale() {
		return scale;
	}

	public List<CNode> getNodes() {
		return children;
	}
	
	public CInstanceController getInstanceController() {
		return instanceController;
	}
	
	public CInstanceGeometry getInstanceGeometry() {
		return instanceGeometry;
	}
	
}
