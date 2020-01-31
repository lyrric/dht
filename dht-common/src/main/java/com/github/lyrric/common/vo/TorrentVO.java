package com.github.lyrric.common.vo;

import com.github.lyrric.common.entity.Torrent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @Builder
public class TorrentVO implements Serializable {

	private Torrent torrent;
	private List<Torrent> similar;
}
