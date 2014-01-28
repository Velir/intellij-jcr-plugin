package velir.intellij.cq5.config;

import java.util.regex.Pattern;

public class JCRMountPoint {
	private static final String DEFAULT_FILESYSTEM_MOUNT_POINT = "";
	private static final String DEFAULT_JCR_MOUNT_POINT = "/apps";

	public String fileSystemMountPoint;
	public String jcrNode;

	public JCRMountPoint () {
		this(DEFAULT_FILESYSTEM_MOUNT_POINT, DEFAULT_JCR_MOUNT_POINT);
	}

	public JCRMountPoint (JCRMountPoint other) {
		fileSystemMountPoint = other.getFileSystemMountPoint();
		jcrNode = other.getJcrNode();
	}

	public JCRMountPoint (String fileSystemMountPoint, String jcrNode) {
		this.fileSystemMountPoint = fileSystemMountPoint;
		this.jcrNode = jcrNode;
	}

	public String getFileSystemMountPoint() {
		return fileSystemMountPoint;
	}

	public String getJcrNode() {
		return jcrNode;
	}

	public void setFileSystemMountPoint(String fileSystemMountPoint) {
		this.fileSystemMountPoint = fileSystemMountPoint;
	}

	public void setJcrNode(String jcrNode) {
		this.jcrNode = jcrNode;
	}

	/**
	 * converts those pesky windows path separators
	 * @return
	 */
	public String getFileSystemMountPointEscaped() {
		return fileSystemMountPoint.replaceAll("\\\\", "/");
	}

	/**
	 * Find the path to a jcr node by taking the path to a file, subtracting the filesystem mount point prefix
	 * and adding the jcr mount point to the front
	 *
	 * @param filePath - the path
	 * @return
	 */
	public String getJcrPath (String filePath) {
		String path = jcrNode;
		if (! jcrNode.endsWith("/")) path += "/";
		path += filePath.replaceFirst(Pattern.quote(getFileSystemMountPointEscaped()), "").replaceFirst("^/", "");
		return path;
	}

	/**
	 * tells if a file is contains the filesystem mount point
	 * @param filePath
	 * @return
	 */
	public boolean contains(String filePath) {
		return filePath.startsWith(getFileSystemMountPointEscaped());
	}

	public boolean equals (Object o) {
		if (! (o instanceof JCRMountPoint)) return false;

		JCRMountPoint other = (JCRMountPoint) o;
		return other.fileSystemMountPoint.equals(fileSystemMountPoint)
				&& other.jcrNode.equals(jcrNode);
	}
}
