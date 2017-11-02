package nl.knaw.huygens.textmod.core.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A collection of tokens.
 */
public class Tokens implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<String, Token> tokens;

  public Tokens() {
    tokens = Maps.newHashMap();
  }

  public Token get(String key) {
    return tokens.get(key);
  }

  public Set<String> getKeys() {
    return tokens.keySet();
  }

  public void put(Token token) {
    tokens.put(token.getText(), token);
  }

  public void increment(String key, long value) {
    Token token = tokens.get(key);
    if (token == null) {
      token = new Token(key);
      tokens.put(key, token);
    }
    token.increment(value);
  }

  public void increment(String key) {
    increment(key, 1);
  }

  public void increment(String[] keys) {
    for (String key : keys) {
      increment(key, 1);
    }
  }

  public long getTotalTokenCount() {
    long total = 0;
    for (Map.Entry<String, Token> entry : tokens.entrySet()) {
      total += entry.getValue()
                    .getCount();
    }
    return total;
  }

  public long getUniqueTokenCount() {
    return tokens.size();
  }

  public long getCountFor(String key) {
    Token token = tokens.get(key);
    return (token != null) ? token.getCount() : 0;
  }

  public double getValueFor(String key) {
    Token token = tokens.get(key);
    return (token != null) ? token.getValue() : 0.0;
  }

  public void handleSorted(TokenHandler handler, Comparator<Token> comparator) {
    List<Token> list = Lists.newArrayList(tokens.values());
    Collections.sort(list, comparator);
    for (Token token : list) {
      if (!handler.handle(token)) {
        break;
      }
    }
  }

  public void handle(TokenHandler handler) {
    for (Token token : tokens.values()) {
      if (!handler.handle(token)) {
        break;
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void read(File file) throws IOException {
    try {
      ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
      tokens = (Map<String, Token>) stream.readObject();
      stream.close();
    } catch (ClassNotFoundException e) {
      throw new IOException("Failed to read tokens", e);
    }
  }

  public void write(File file) throws IOException {
    ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
    stream.writeObject(tokens);
    stream.close();
  }

}
