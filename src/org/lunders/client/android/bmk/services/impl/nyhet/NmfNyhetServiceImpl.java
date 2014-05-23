/*
 * Copyright 2014 Anders Lund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lunders.client.android.bmk.services.impl.nyhet;

import android.util.Log;
import org.lunders.client.android.bmk.model.nyheter.Nyhet;
import org.lunders.client.android.bmk.model.nyheter.Nyhetskilde;
import org.lunders.client.android.bmk.services.NyhetService;
import org.lunders.client.android.bmk.services.impl.AbstractServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class NmfNyhetServiceImpl extends AbstractServiceImpl implements NyhetService {

	private static final String TAG = NmfNyhetServiceImpl.class.getSimpleName();

	private static String testBeskrivelse = "Morbi tristique diam ut orci lobortis, sit amet dictum orci dictum. Cras id libero sapien. Aliquam velit justo, ornare vel eros ac, ornare ultricies arcu. Ut nunc mauris, lobortis in tortor varius, malesuada mattis libero. Ut mollis diam sed dui laoreet tristique. Aliquam quam neque, imperdiet nec urna eget, vestibulum bibendum metus. Pellentesque quis consectetur purus. Donec a nisi non nisi viverra ultricies non at metus. Suspendisse tristique lectus vel tortor ullamcorper interdum. Duis eget vehicula erat, non elementum magna. Nam eu massa risus.\n" +
		"\n" +
		"Proin ut ultrices orci. Nulla vel varius arcu. Donec at sapien id neque auctor venenatis. Nullam vel scelerisque risus, sit amet ullamcorper tortor. Pellentesque id diam auctor, sagittis felis et, sollicitudin orci. Nulla facilisi. Morbi eu venenatis libero, vitae condimentum nibh. Quisque porttitor erat sapien, eu congue elit fermentum non. Nullam consectetur ornare mauris at blandit. Aliquam eu ante a nibh mollis placerat.\n" +
		"\n" +
		"In sagittis ultricies egestas. Proin placerat euismod congue. Suspendisse quis massa a elit gravida posuere. Praesent adipiscing, lectus sed lobortis laoreet, neque magna sollicitudin massa, vel lacinia arcu eros non mauris. Donec id adipiscing metus. Curabitur sit amet est non enim hendrerit egestas ut eu libero. Quisque ultrices eget neque quis cursus. Nam elementum, quam eu dignissim sodales, neque arcu consequat sapien, ullamcorper semper purus arcu eu mi. Nullam varius nibh nibh, id imperdiet lacus vestibulum et. Nulla nec condimentum est. Maecenas eu consequat metus.";


    @Override
    public List<Nyhet> hentNyheter() {
	    Log.i(TAG, "Henter nyheter fra NMF...");

	    List l = new ArrayList<>();
	    l.add(new Nyhet("Vivamus consectetur", testBeskrivelse , Nyhetskilde.NMF));

	    Log.i(TAG, "Nyheter hentet");
	    return l;
    }

	@Override
	public void hentNyhet(Nyhet nyhet) {

	}
}
