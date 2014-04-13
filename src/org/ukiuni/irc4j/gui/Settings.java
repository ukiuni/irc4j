package org.ukiuni.irc4j.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "config")
public class Settings {
	static {
		new File(System.getProperty("user.home") + "/irc4j/").mkdirs();
	}
	public static final File CONFIG_FILE = new File(System.getProperty("user.home"), "/irc4j/irc_connect_setting.xml");

	private List<HostSetting> hosts = new ArrayList<HostSetting>();

	public List<HostSetting> getHosts() {
		return hosts;
	}

	public void setHosts(List<HostSetting> hosts) {
		this.hosts = hosts;
	}

	@XmlTransient
	Map<String, HostSetting> hostMap = new HashMap<String, HostSetting>();

	public static Settings load() throws IOException {
		try {
			Settings settings = JAXB.unmarshal(CONFIG_FILE, Settings.class);
			for (HostSetting host : settings.hosts) {
				settings.hostMap.put(host.getHostUrl() + ":" + host.getPort(), host);
			}
			return settings;
		} catch (Exception e) {
			return new Settings();
		}
	}

	public void save() throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(Settings.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			OutputStream os = new FileOutputStream(CONFIG_FILE);
			marshaller.marshal(this, os);
			os.close();
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	public void addHost(String serverPath, int port, String nickname, String localHostname) {
		HostSetting host = new HostSetting(serverPath, port, serverPath, nickname, localHostname, nickname);
		hosts.add(host);
		hostMap.put(serverPath + ":" + port, host);
	}

	public HostSetting getHostSetting(String serverPath, int port) {
		return hostMap.get(serverPath + ":" + port);
	}

	public Collection<HostSetting> getHostSettings() {
		return hostMap.values();
	}
}
