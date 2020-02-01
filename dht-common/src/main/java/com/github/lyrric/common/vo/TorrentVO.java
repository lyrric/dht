package com.github.lyrric.common.vo;

import com.github.lyrric.common.entity.TorrentInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @Builder
public class TorrentVO implements Serializable {

	private TorrentInfo torrent;
	private List<TorrentInfo> similar;
}
